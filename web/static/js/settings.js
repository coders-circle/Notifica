$(document).ready(function(){
    $('body').on( 'click', '.settings-header', function(e){
        $('.settings-content').hide();
        target = $('#' + $(this).data('target'));
        target.fadeIn();
        $('.settings-header').removeClass('active');
        $(this).addClass('active')
    });

    $('body').on('click', '.expandable-menu .edit-btn', function(e){
        var container = $(this).parent().parent().parent();
        container.find('.visible-content').slideUp();
        container.find('.hidden-content').slideDown();
    });
    $('body').on('click', '.expandable-menu input[type=button]', function(e){
        e.preventDefault();
        var container = $(this).parent().parent().parent();
        container.find('.visible-content').slideDown();
        container.find('.hidden-content').slideUp();
    });
    $('body').on('click', '.expandable-menu .visible-content', function(e){
        var container = $(this).parent();
        container.find('.visible-content').slideUp();
        container.find('.hidden-content').slideDown();
    });

});
