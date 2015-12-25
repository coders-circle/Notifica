jQuery(document).ready(function($) {
	$('body').on( 'click', '.btn-add-period', function(e){
        var period_template = $('.template-period').clone();
        period_template.addClass('period');
        period_template.removeClass('template-period');
        period_template.removeClass('hidden');
        period_template.appendTo($(this).parent().parent().find('.periods'));
    });
});
