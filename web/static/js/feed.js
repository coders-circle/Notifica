jQuery(document).ready(function($) {
    var defaultPosts = [];
    var queryPosts = [];

    var typewatch = (function(){
        var timer = 0;
        return function(callback, ms){
            clearTimeout (timer);
            timer = setTimeout(callback, ms);
        };
    })();
    function removeOldPosts(){
        var old_posts = $('.userpost');
        if(old_posts){
            old_posts.remove();
            $('.posts hr').remove();
        }
        var msg = $('.posts .msg');
        if(msg){
            msg.remove();
        }
    }
    function renderPosts(posts){
        var posts_container = $('.posts');
        var post_template = $('.template-userpost').clone();
        var tag_template = $('<span class="tag"></span>');
        for(var i = 0; i < posts.length; i++){
            var post = post_template.clone();
            post.find('.num').text(posts[i].num_comments);
            post.find('.user-avatar').attr('src', '/static/img/ninja.png');
            post.find('.title').text(posts[i].title);
            post.find('.user-name').text(
                posts[i].posted_by.first_name?
                    posts[i].posted_by.first_name :
                    posts[i].posted_by.username
                );
            var posted_at = new Date(posts[i].posted_at);
            post.find('.time').text(posted_at.toLocaleString());
            var tags_container = post.find('.tags');
                if(posts[i].tags){
                var tags = $.parseJSON(posts[i].tags);
                for(var j = 0; j < tags.length; j++){
                    var tag = tag_template.clone();
                    tag.text(tags[j]);
                    tag.appendTo(tags_container);
                }
            }
            post.find('.content').text(posts[i].body);
            post.removeClass('template-userpost');
            post.addClass('userpost');
            post.appendTo(posts_container);
            post.hide();
            post.removeClass('hidden');
            post.fadeIn();
            if(i != posts.length-1){
                posts_container.append('<hr>');
            }
        }
    }
    function onSuccess(res){
        if(res.length > 0){
            renderPosts(res);
        } else {
            var empty_msg = $('#empty-msg').clone();
            empty_msg.removeClass('hidden');
            empty_msg.appendTo($('.posts'));
        }
        $('#posts-loading-animation').fadeOut();
    }
    function onFailure(){
        var error_msg = $('#error-msg').clone();
        error_msg.removeClass('hidden');
        error_msg.appendTo($('.posts'));
        $('#posts-loading-animation').fadeOut();
    }
    function onLoad(){
        $('#posts-loading-animation').show();
        $.ajax({
            url: '/feed/api/v1/posts/',
            type: 'GET',
            error: function() {
                onFailure();
            },
            success: function(res) {
                onSuccess(res);
            }
        });
    }

    var last_search_str = "";
    var search_event_count = 0;
    $("#search-post-input").keyup(function(){
        var search_str = $("#search-post-input").val();
        if( search_str != last_search_str){
            last_search_str = search_str;
            typewatch(function(){
                var current_search_event_count = ++search_event_count;
                removeOldPosts();
                $('#posts-loading-animation').fadeIn();
                $.ajax({
                    url: '/feed/api/v1/posts/?q='+$("#search-post-input").val(),
                    type: 'GET',
                    error: function() { onFailure(); },
                    success: function(res) {
                        if( current_search_event_count >= search_event_count ) {
                            onSuccess(res);
                        }
                        --search_event_count;
                    }
                });
            }, 500);
        }
    });

    var total_search_count = 0;
    var num_posts = 0;
    function queryPosts(search_str){
        typewatch(function(){
            var search_count = ++total_search_count;
            removeOldPosts();
            $('#posts-loading-animation').fadeIn();
            $.ajax({
                url: '/feed/api/v1/posts/?q='+search_str,
                type: 'GET',
                error: function() { onFailure(); },
                success: function(res) {
                    if( search_count >= total_search ) {
                        onSuccess(res);
                    }
                    --search_event_count;
                }
            });

        }, 500);
    }

    onLoad();
});
