jQuery(document).ready(function($) {
	$('body').on( 'click', '.ripple-effect', function(e){
		var ripple_effect_wrap = $('<span class="ripple-effect-wrap"></span>');
		ripple_effect_wrap.css({
			'width' : $(this).outerWidth(),
			'height' : $(this).outerHeight(),
			'position' : 'absolute',
			'top' : $(this).offset().top,
			'left' : $(this).offset().left,
			'z-index' : 98,
			'overflow' : 'hidden',
			'background-clip' : 'padding-box',
			'border-radius'	: 0
		});
		ripple_effect_wrap.appendTo('body');
		var click_x_ripple = e.pageX - $(this).offset().left;
		var click_y_ripple = e.pageY - $(this).offset().top;
		var circular_width = 1000;

		var ripple = $('<span class="ripple"></span>');
		ripple.css({
			'width' : circular_width,
			'height' : circular_width,
			'background' : 'rgba( 0, 0, 0, 0.1 )',
			'position' : 'absolute',
			'top' : click_y_ripple - ( circular_width / 2 ),
			'left' : click_x_ripple - ( circular_width / 2 ),
			'content' : '',
		    'background-clip' : 'padding-box',
		    'border-radius' : '50%',
		    'animation-name' : 'ripple-animation',
		    'animation-duration' : '1s',
		    'animation-fill-mode' : 'both',
			'cursor' : 'pointer'
		});
		$('.ripple-effect-wrap:last').append( ripple );
		setTimeout( function(){
			ripple_effect_wrap.fadeOut(function(){
				$(this).remove();
			});
		}, 100 );
	});
});
