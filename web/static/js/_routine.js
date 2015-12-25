jQuery(document).ready(function($) {
	$('body').on( 'click', '.btn-add-subject', function(e){
        var se_details = $('.subject-element-details');
    });


	$('body').on( 'click', '.subject-element', function(e){
		var se_backdrop = $('<div class="se-backdrop"></div>');
		se_backdrop.appendTo('body');

		var se = $(this);
		var day = se.parent().data('index');
		var period = se.data('index');
		var subject = routine[day][period]['subject'];
		alert(subject);

		var se_details = $('.subject-element-details');
		se_details.css("top", $(this).offset().top - $(window).scrollTop());
		se_details.css("left", $(this).offset().left - $(window).scrollLeft());
		se_details.css("width", $(this).outerWidth());
		se_details.css("height", $(this).outerHeight());

		jQuery.data(se_details, "data-left", $(this).offset().left - $(window).scrollLeft());
		jQuery.data(se_details, "data-top", $(this).offset().top - $(window).scrollTop());
		jQuery.data(se_details, "data-width", $(this).outerWidth());
		jQuery.data(se_details, "data-height", $(this).outerHeight());

		se_details.show();
		se_details.addClass("se-shown");

		var se_detail_content = $('.se-details-content');
		se_detail_content.find(".se-subject-name").val($.trim($(this).find(".subject").text()));


		setTimeout(function(){
			$('.se-details-content').fadeIn();
		}, 500);
    });
});

$(document).mouseup(function (e)
{
    var container = $(".se-shown");
    if (!container.is(e.target) && container.has(e.target).length === 0) {
		$('.se-details-content').fadeOut();
		container.removeClass("se-shown");
		container.addClass('se-hidden');
		setTimeout(function(){
			container.removeClass('se-hidden');
			container.hide();
		}, 400);
		$('.se-backdrop').fadeOut();
    }
});
