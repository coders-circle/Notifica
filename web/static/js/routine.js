jQuery(document).ready(function($) {
	$('#subject-input').selectize({
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

	$('#teachers-input').selectize({
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



	$('body').on( 'click', '.btn-add-period', function(e){
		//$('.backdrop').fadeIn();
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('show');
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
		$('.backdrop').fadeIn();
		var add_subject_dialog = $('body').find(".add-subject-dialog");
		add_subject_dialog.data("period", $(this));
		add_subject_dialog.data("new-period", false);
		add_subject_dialog.find(".input-subject").val($(this).find(".subject").text());
		add_subject_dialog.find(".input-teachers").val($(this).find(".teachers").text());
		add_subject_dialog.find(".input-remarks").val($(this).find(".remarks").text());
		setTimeout(function(){
			add_subject_dialog.addClass("shown");
			add_subject_dialog.show();
		}, 200);
	});
	$('body').on( 'click', '.btn-dlg-ok', function(e){
		var add_subject_dialog = $(this).parent().parent();

		var subject_input = add_subject_dialog.find(".input-subject");
		var teachers_input = add_subject_dialog.find(".input-teachers");
		var remarks_input = add_subject_dialog.find(".input-remarks");

		if(add_subject_dialog.data("new-period")){
			var period_template = $('.template-period').clone();
	        period_template.addClass('period');
	        period_template.removeClass('template-period');
	        period_template.removeClass('hidden');
			var subject = period_template.find('.subject');
			var teachers = period_template.find('.teachers');
			var remarks = period_template.find('.remarks');
			subject.text(subject_input.val());
			teachers.text(teachers_input.val());
			remarks.text(remarks_input.val());
			period_template.appendTo(add_subject_dialog.data("period-container"));
			if(remarks_input.val().length > 0){
				remarks.show();
			}else{
				remarks.hide();
			}
		}else{
			var period = add_subject_dialog.data("period");
			period.find(".subject").text(subject_input.val());
			period.find(".teachers").text(teachers_input.val());
			period.find(".remarks").text(remarks_input.val());
			if(remarks_input.val().length > 0){
				period.find(".remarks").show();
			}else{
				period.find(".remarks").hide();
			}

		}

		add_subject_dialog.removeClass("shown");
		add_subject_dialog.hide();
		$('.backdrop').fadeOut();
	});
	$('body').on( 'click', '.btn-dlg-cancel', function(e){
		var add_subject_dialog = $(this).parent().parent();
		add_subject_dialog.removeClass("shown");
		add_subject_dialog.hide();
		$('.backdrop').fadeOut();
	});
});

$(document).mouseup(function (e){
	var container = $(".shown");
	if (!container.is(e.target) && container.has(e.target).length === 0) {
		container.removeClass("shown");
		container.hide();
		$('.backdrop').fadeOut();
	}
});
