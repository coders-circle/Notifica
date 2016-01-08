jQuery(document).ready(function($) {
    $('#drawer-open-btn').click(function(e){
        $('.navigation-drawer').addClass('drawer-shown');
        $('.backdrop').show();
    });
    $('#drawer-close-btn').click(function(e){
        $('.backdrop').hide();
        $('.navigation-drawer').removeClass('drawer-shown');
    });
    $('.backdrop').click(function(e){
        $('.backdrop').hide();
        $('.navigation-drawer').removeClass('drawer-shown');
    });

});
