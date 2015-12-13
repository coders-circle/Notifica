jQuery(document).ready(function($) {
    $('body').on( 'click', '.expand-button', function(e){
        $(this).parent().parent().find(".list-comment").fadeIn();
    });
    $('body').on( 'click', '.card-post', function(e){
        $(this).find(".list-comment").fadeIn();
    });

});
