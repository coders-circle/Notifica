jQuery(document).ready(function($) {

	var $subject_select = $('#subject-input').selectize({
		placeholder: 'Subject',
        valueField: 'name',
        labelField: 'name',
        searchField: ['name'],
        sortField: 'name',
        create: true,
		persist: false,
		maxItems: 1,
		closeAfterSelect: true,
        render: {
            option: function(item, escape) {
				return '<div class="li-suggestion">' +
                    (item.name ? '<span class="name">' + escape(item.name) + '</span>' : '') +
                '</div>';
            }
        },
        load: function(query, callback) {
            if (!query.length) return callback();
            $.ajax({
                url: '/classroom/api/v1/subjects/?format=json&q=' + encodeURIComponent(query),
                type: 'GET',
                error: function() {
                    error="cant fetch the suggestions";
                    callback(error);
                },
                success: function(res) {
                    callback(res.slice(0, 10));
                }
            });
        }
    });


	var $teachers_select = $('#teachers-input').selectize({
		placeholder: 'Teachers',
		labelField: 'name',
        valueField: 'notifica_id',
        searchField: ['name', 'username'],
        sortField: 'name',
		persist: false,
		closeAfterSelect: true,
        create: function(input){
			return{
				notifica_id: getNotificaID(),
				name: input
			}
		},
        render: {
            option: function(item, escape) {
				return '<div class="li-suggestion">' +
                    (item.name ? '<span class="name">' + escape(item.name) + '&nbsp;</span>' : '') +
                    (item.username ? '<br><span class="username">' + escape(item.username) + '</span>' : '') +
                    (item.email ? '<br><span class="email">' + escape(item.email) + '</span>' : '') +
                '</div>';
            }
        },
		onItemAdd: function(value, $item){
			//console.log($item.text());
		},
        load: function(query, callback) {
            if (!query.length) return callback();
            $.ajax({
                url: '/classroom/api/v1/teachers/?format=json&q=' + encodeURIComponent(query),
                type: 'GET',
                error: function() {
                    error="cant fetch the suggestions";
                    callback(error);
                },
                success: function(res) {
					var newRes = res.slice(0, 10);
					var newArr = [];
					for (var i = 0; i < newRes.length; i++ ){
						var item = newRes[i];
						newArr.push({
							notifica_id: item.notifica_id,
							username: item.user.username,
							name: item.user.first_name + ' ' + item.user.last_name,
							email: item.user.email
						});
					}
					//console.log(res[0].user.username);
                    callback(newArr);
                }
            });
        }
    });

	var teachers_control = $teachers_select[0].selectize;
	var subject_control = $subject_select[0].selectize;

	$('body').on( 'click', '.btn-add-period', function(e){
		//$('.backdrop').fadeIn();
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		var start_time = add_subject_dialog.find(".input-start-time");
		var end_time = add_subject_dialog.find(".input-end-time");
		var remarks = add_subject_dialog.find(".input-remarks");

		start_time.val("");
		end_time.val("");
		remarks.val("");

		teachers_control.clear();
		subject_control.clear();

		// subject_control.createItem('test');
		// teachers_control.addOption({
		// 	username:'fhx',
		// 	email:'',
		// 	first_name:'Ankit',
		// 	last_name:'Mehta'
		// });
		// teachers_control.addItem('fhx');

		//add_subject_dialog.find(".input-subject").val("");
		//add_subject_dialog.find(".input-teachers").val("");
		//add_subject_dialog.find(".input-remarks").val("");
		add_subject_dialog.data("period-container", $(this).parent().parent().find('.periods'));
		add_subject_dialog.data("new-period", true);
		// setTimeout(function(){
		// 	add_subject_dialog.addClass("shown");
		// 	add_subject_dialog.show();
		// }, 200);

		add_subject_dialog.modal('show');

        // var period_template = $('.template-period').clone();
        // period_template.addClass('period');
        // period_template.removeClass('template-period');
        // period_template.removeClass('hidden');
		// var subject = period_template.find('.subject');
		// var teachers = period_template.find('.teachers');
		// var remarks = period_template.find('.remarks');
		// remarks.text('jpt :D');
        // period_template.appendTo($(this).parent().parent().find('.periods'));
    });
	$('body').on( 'click', '.period', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		var ip_start_time = add_subject_dialog.find(".input-start-time");
		var ip_end_time = add_subject_dialog.find(".input-end-time");
		var ip_remarks = add_subject_dialog.find(".input-remarks");

		ip_start_time.val("");
		ip_end_time.val("");
		ip_remarks.val("");
		teachers_control.clear();
		subject_control.clear();

		add_subject_dialog.data("new-period", false);
		add_subject_dialog.data("period", $(this));

		var period = $(this);
		var subject = period.find('.subject');
		var teachers = period.find('.teachers');
		var remarks = period.find('.remarks');

		var array_teachers = teachers.data("array-teachers");
		for(var i=0; i < array_teachers.length; i++){
			teachers_control.createItem({
				notifica_id: array_teachers[i].notifica_id,
				name: array_teachers[i].name
			});
			teachers_control.addItem(array_teachers[i].notifica_id);
		}
		subject_control.createItem(subject.text());
		ip_remarks.val(remarks.text());


		add_subject_dialog.modal('show');
	});
	$('body').on( 'click', '.btn-dlg-ok', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		if(add_subject_dialog.data("new-period")){
			var period = $('.template-period').clone();
	        period.addClass('period');
	        period.removeClass('template-period');
	        period.removeClass('hidden');

			var subject = period.find('.subject');
			var teachers = period.find('.teachers');
			var remarks = period.find('.remarks');

			// var start_time = add_subject_dialog.find(".input-start-time");
			// var end_time = add_subject_dialog.find(".input-end-time");
			var ip_remarks = add_subject_dialog.find(".input-remarks");
			remarks.text(ip_remarks.val());
			var str_teachers = "";
			var arr_teachers = [];
			for(var i=0; i < teachers_control.items.length; i++){
				arr_teachers.push({notifica_id:teachers_control.items[i],
					name: teachers_control.getItem(teachers_control.items[i]).text()});
				str_teachers += teachers_control.getItem(teachers_control.items[i]).text();
				if(i!=teachers_control.items.length-1) str_teachers += ", "
			}
			teachers.data("array-teachers", arr_teachers);
			teachers.text(str_teachers);
			subject.text(subject_control.items[0]);

	        period.appendTo(add_subject_dialog.data("period-container"));
		}else{
			period = add_subject_dialog.data("period");
			var subject = period.find('.subject');
			var teachers = period.find('.teachers');
			var remarks = period.find('.remarks');

			// var start_time = add_subject_dialog.find(".input-start-time");
			// var end_time = add_subject_dialog.find(".input-end-time");
			var ip_remarks = add_subject_dialog.find(".input-remarks");
			remarks.text(ip_remarks.val());
			var str_teachers = "";
			var arr_teachers = [];
			for(var i=0; i < teachers_control.items.length; i++){
				arr_teachers.push({notifica_id:teachers_control.items[i],
					name: teachers_control.getItem(teachers_control.items[i]).text()});
				str_teachers += teachers_control.getItem(teachers_control.items[i]).text();
				if(i!=teachers_control.items.length-1) str_teachers += ", "
			}
			teachers.data("array-teachers", arr_teachers);
			teachers.text(str_teachers);
			subject.text(subject_control.items[0]);
		}
		add_subject_dialog.modal('hide');
	});
	$('body').on( 'click', '.btn-dlg-cancel', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('hide');
	});
});
