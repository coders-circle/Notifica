jQuery(document).ready(function($) {
	$('body').on( 'click', '.subject-element', function(e){
		var se_backdrop = $('<div class="se-backdrop"></div>');
		se_backdrop.appendTo('body');

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



		setTimeout(function(){
			$('.se-details-content').fadeIn();
		}, 500);
    });
});

$(document).mouseup(function (e)
{
    var container = $(".se-shown");
    if (!container.is(e.target) && container.has(e.target).length === 0) {
		//se_details.css("animation-direction", "reverse");
		//container.hide();
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
