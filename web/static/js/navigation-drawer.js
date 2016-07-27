jQuery(document).ready(function($) {
    $('#drawer-open-btn').click(function(e){
        $('#navigation-drawer').addClass('drawer-shown');
        $('.backdrop').show();
        $('#body').css('webkit-filter', 'blur(2px)');
    });
    $('#drawer-close-btn').click(function(e){
        $('.backdrop').hide();
        $('#navigation-drawer').removeClass('drawer-shown');
        $('#body').css('webkit-filter', 'none');
    });
    $('.backdrop').click(function(e){
        $('.backdrop').hide();
        $('#navigation-drawer').removeClass('drawer-shown');
        $('#body').css('webkit-filter', 'none');
    });

});
