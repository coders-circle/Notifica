$(document).ready(function(){
    $('body').on( 'click', '.settings-header', function(e){
        $('.settings-content').hide();
        target = $('#' + $(this).data('target'));
        target.fadeIn();
        $('.settings-header').removeClass('active');
        $(this).addClass('active')
    });
});
