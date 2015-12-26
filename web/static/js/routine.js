jQuery(document).ready(function($) {

	var $subject_select = $('#subject-input').selectize({
		placeholder: 'Subject',
        valueField: 'name',
        labelField: 'name',
        searchField: ['name'],
        sortField: 'name',
        create: true,
		maxItems: 1,
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
        valueField: 'email',
        labelField: 'username',
        searchField: ['username', 'first_name', 'last_name', 'email'],
        sortField: 'username',
        create: true,
        render: {
            option: function(item, escape) {
				return '<div class="li-suggestion">' +
                    (item.username ? '<span class="username">' + escape(item.username) + '</span>' : '') +
                    '<br>'+
                    (item.first_name ? '<span class="name">' + escape(item.first_name) + '&nbsp;</span>' : '') +
                    (item.last_name ? '<span class="name">' + escape(item.last_name) + '</span>' : '') +
                    (item.first_name || item.last_name? '<br>':'') +
                    (item.email ? '<span class="email">' + escape(item.email) + '</span>' : '') +
                '</div>';
            }
        },

        load: function(query, callback) {
            if (!query.length) return callback();
            $.ajax({
                url: '/classroom/api/v1/users/?format=json&q=' + encodeURIComponent(query),
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

	var teachers_control = $teachers_select[0].selectize;
	var subject_control = $subject_select[0].selectize;

	$('body').on( 'click', '.btn-add-period', function(e){
		//$('.backdrop').fadeIn();
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('show');
		var start_time = add_subject_dialog.find(".input-start-time");
		var end_time = add_subject_dialog.find(".input-end-time");
		var subject = add_subject_dialog.find("#subject-input");
		var teachers = add_subject_dialog.find("#teachers-input");
		var remarks = add_subject_dialog.find(".input-remarks");

		start_time.val("");
		end_time.val("");
		subject.val("");
		teachers.val("");
		remarks.val("");
		teachers_control.clear();
		subject_control.clear();

		//add_subject_dialog.find(".input-subject").val("");
		//add_subject_dialog.find(".input-teachers").val("");
		//add_subject_dialog.find(".input-remarks").val("");
		//add_subject_dialog.data("period-container", $(this).parent().parent().find('.periods'));
		//add_subject_dialog.data("new-period", true);
		// setTimeout(function(){
		// 	add_subject_dialog.addClass("shown");
		// 	add_subject_dialog.show();
		// }, 200);


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
	});
	$('body').on( 'click', '.btn-dlg-ok', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('hide');
	});
	$('body').on( 'click', '.btn-dlg-cancel', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('hide');
	});
});
