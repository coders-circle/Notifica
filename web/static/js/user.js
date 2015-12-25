jQuery(document).ready(function($) {
    $('body').on('click', '.btn-toggle-comments', function(e){
        var list_comment = $(this).parent().parent().parent().find(".comments");
        var chevron = $(this).find("i");
        if(list_comment.data("hidden") == "false"){
            list_comment.fadeOut();
            list_comment.data("hidden", "true");
            chevron.removeClass("fa-chevron-up");
            chevron.addClass("fa-chevron-down");
        }else{
            list_comment.fadeIn();
            list_comment.data("hidden", "false");
            chevron.removeClass("fa-chevron-down");
            chevron.addClass("fa-chevron-up");
        }
    });
    $('body').on( 'click', '.card-post', function(e){
        //$(this).find(".list-comment").fadeIn();

    });

});
