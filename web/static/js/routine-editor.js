var routine_dirty = false;
var newID = -1;
selected_group = $('#group-select select');
function getNewID(){
	return newID--;
}

var subject_select = null;
var teachers_select = null
var teachers_control = null;
var subject_control = null;

function getMinutes(formattedTimeStr){
	timeSlices = formattedTimeStr.split(':');
	return parseInt(timeSlices[0]*60) + parseInt(timeSlices[1]);
}
function renderSelectedRoutine(){
    filterRoutine($('#group-select select').val());
    calculateMinMaxTime();
    renderTimeLine();
    renderPeriods();
}

$(document).ready(function(){
	subject_select = $('#subject-input').selectize({
		placeholder: 'Subject',
		valueField: 'id',
		labelField: 'name',
		searchField: ['name'],
		sortField: 'name',
		openOnFocus: false,
		maxItems: 1,
		closeAfterSelect: true,
		create: function(input){
			return{
				id: getNewID(),
				name: input
			}
		},
		render: {
			option: function(item, escape) {
				return '<div class="li-suggestion">' +
					(item.name ? '<span class="name">' + escape(item.name) + '</span>' : 'oops! something is wrong') +
				'</div>';
			}
		},
		load: function(query, callback) {
			if (!query.length) return callback();
			$.ajax({
				url: '/classroom/api/v1/subjects/?format=json&q=' + encodeURIComponent(query),
				type: 'GET',
				error: function() {
					callback();
				},
				success: function(res) {
					callback(res.slice(0, 10));
				}
			});
		}
	});
	teachers_select = $('#teachers-input').selectize({
		placeholder: 'Teachers',
		labelField: 'name',
		valueField: 'id',
		searchField: ['name', 'email'],
		sortField: 'name',
		closeAfterSelect: true,
		openOnFocus: false,
		create: function(input){
			return{
				id: getNewID(),
				name: input
			}
		},
		render: {
			option: function(item, escape) {
				return '<div class="li-suggestion">' +
					(item.name ? '<span class="name">' + escape(item.name) + '&nbsp;</span>' : '') +
					(item.email ? '<br><span class="email">' + escape(item.email) + '</span>' : '') +
				'</div>';
			}
		},
		load: function(query, callback) {
			if (!query.length) return callback();
			$.ajax({
				url: '/classroom/api/v1/teachers/?format=json&q=' + encodeURIComponent(query),
				type: 'GET',
				error: function() {
					callback();
				},
				success: function(res) {
					var newRes = res.slice(0, 10);
					var newArr = [];
					for (var i = 0; i < newRes.length; i++ ){
						var item = newRes[i];
						newArr.push({
							id: item.id,
							name: item.user? item.user.first_name + ' ' + item.user.last_name: item.username,
							email: item.user? item.user.email: null
						});
					}
					callback(newArr);
				}
			});
		}
	});
	teachers_control = teachers_select[0].selectize;
	subject_control = subject_select[0].selectize;
	$('body').on( 'click', '.period', function(e){
		var add_subject_dialog = $('body').find("#add-period-dialog");
		add_subject_dialog.data("new-period", false);
		add_subject_dialog.data("id-period", $(this).data('id'));
		var day_index = $(this).parent().parent().parent().data('index-day');
		add_subject_dialog.data("index-day", day_index);
		var periods = routine[day_index];
		var period = $(this);
		var period_data = $.grep(periods, function(item){
			return item.id == period.data('id');
		})[0];

		var ip_start_time = add_subject_dialog.find(".input-start-time");
		var ip_end_time = add_subject_dialog.find(".input-end-time");
		var ip_remarks = add_subject_dialog.find(".input-remarks");

		ip_start_time.val(getFormattedTimeString(period_data.start_time));
		ip_end_time.val(getFormattedTimeString(period_data.end_time));
		ip_remarks.val(period_data.remarks);
		teachers_control.clear();
		subject_control.clear();
		for(var i=0; i < period_data.teachers.length; i++){
			teachers_control.addOption(period_data.teachers[i]);
			teachers_control.addItem(period_data.teachers[i].id);
		}
		subject_control.addOption(period_data.subject);
		subject_control.addItem(period_data.subject.id);
		var group_selection_container = add_subject_dialog.find(".group-selection");
		for(var i=0; i<group_list.length; i++){
			group_selection_container.find('#'+group_list[i].id).prop('checked', false);
		}
		for(var i=0; i<period_data.groups.length; i++){
			group_selection_container.find('#'+period_data.groups[i].id).prop('checked', true);
		}
		add_subject_dialog.modal('show');
	});
	$('body').on( 'click', '.btn-add-period', function(e){
		var add_subject_dialog = $('body').find("#add-period-dialog");
		var start_time = add_subject_dialog.find(".input-start-time");
		var end_time = add_subject_dialog.find(".input-end-time");
		var remarks = add_subject_dialog.find(".input-remarks");

		start_time.val("");
		end_time.val("");
		remarks.val("");

		teachers_control.clear();
		subject_control.clear();

		var group_selection_container = add_subject_dialog.find(".group-selection");
		for(var i=0; i<group_list.length; i++){
			group_selection_container.find('#'+group_list[i].id).prop('checked', true);
		}
		add_subject_dialog.data("new-period", true);
		add_subject_dialog.data("index-day", $(this).parent().parent().data('index-day'));
		add_subject_dialog.modal('show');
	});

	$('body').on( 'click', '.btn-dlg-ok', function(e){
		var add_subject_dialog = $('body').find("#add-period-dialog");
		var arr_teachers = [];
		for(var i=0; i < teachers_control.items.length; i++){
			arr_teachers.push({
				id:teachers_control.items[i],
				name: teachers_control.getItem(teachers_control.items[i]).text()
			});
		}
		var obj_subject = {
			id: subject_control.items[0],
			name: subject_control.getItem(subject_control.items[0]).text()
		};
		var day_index = add_subject_dialog.data('index-day');
		var checked_groups = [];
		var group_selection_container = add_subject_dialog.find(".group-selection");
		for(var i=0; i<group_list.length; i++){
			if(group_selection_container.find('#'+group_list[i].id).is(':checked')){
				checked_groups.push(group_list[i]);
			}
		}
		if(add_subject_dialog.data("new-period")){
			var new_period = {
				id: periodsCount++,
				subject: obj_subject,
				teachers: arr_teachers,
				start_time: getMinutes(add_subject_dialog.find(".input-start-time").val()),
				end_time: getMinutes(add_subject_dialog.find(".input-end-time").val()),
				remarks: add_subject_dialog.find(".input-remarks").val(),
				groups: checked_groups
			};
			routine_all[day_index].push(new_period);
		}else{
			var period_data = $.grep(routine[add_subject_dialog.data('index-day')], function(item){
				return item.id == add_subject_dialog.data('id-period');
			})[0];
			period_data.subject = obj_subject;
			period_data.teachers = arr_teachers;
			period_data.start_time = getMinutes(add_subject_dialog.find(".input-start-time").val());
			period_data.end_time = getMinutes(add_subject_dialog.find(".input-end-time").val());
			period_data.remarks = add_subject_dialog.find(".input-remarks").val();
			period_data.groups = checked_groups;
		}
		renderSelectedRoutine();
		add_subject_dialog.modal('hide');
	});

	$('body').on( 'click', '.btn-dlg-cancel', function(e){
		var add_subject_dialog = $('body').find("#add-period-dialog");
		add_subject_dialog.modal('hide');
	});

	$('#group-select select').change(function(){
		renderSelectedRoutine();
	});

});
