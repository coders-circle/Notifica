function acceptRequest(id){
    $.ajax({
        url: '/api/v1/requests/'+id+'/',
        type: 'GET',
        error: function() { onFailure(); },
        success: function(result) {
            result.status=1;
            $.ajax({
                url: '/api/v1/requests/'+id+'/',
                type: 'PUT',
                data:result,
                success: function(e){
                    alert("successful!");
                    $("#request-"+id).remove();
                },
                error: function(e){
                    alert(e.responseText);
                }
            });
            //console.log(result);
        }
    });
}

function rejectRequest(id){

}

$(document).ready(function(){
    var posts = [];
    var query = "?count=5";

    function renderPosts(){
        clearPostsView();
        var posts_container = $('.posts');
        var post_template = $('.template-userpost').clone();
        var tag_template = $('<span class="tag"></span>');
        for(var i = 0; i < posts.length; i++){
            var post = post_template.clone();
            post.find('.num').text(posts[i].num_comments);
            post.find('.num-comments').attr('href', '/feed/post/'+posts[i].id);
            post.find('.user-avatar').attr('src', posts[i].posted_by.avatar);
            post.find('.title').text(posts[i].title);
            post.find('.title').attr('href', '/feed/post/'+posts[i].id);
            post.find('.user-name').text(
                posts[i].posted_by.first_name?
                    posts[i].posted_by.first_name
                        + ' ' + posts[i].posted_by.last_name:
                    posts[i].posted_by.username
                );
            var modified_at = new Date(posts[i].modified_at);
            //post.find('.time').text(modified_at.toLocaleString());
            post.find('.timeago-post').attr('datetime', posts[i].modified_at);
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
        $('.timeago-post').timeago();
    }

    function clearPostsView(){
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

    function clearPosts(){
        posts = [];
        clearPostsView();
    }

    function onSuccess(result){
        if(result.length > 0){
             Array.prototype.push.apply(posts, result);
             renderPosts();
        } else {
            if(posts.length == 0){
                var empty_msg = $('#empty-msg').clone();
                empty_msg.removeClass('hidden');
                empty_msg.appendTo($('.posts'));
            }
        }
        $('#posts-loading-animation').fadeOut();
    }
    function onFailure(){
        var error_msg = $('#error-msg').clone();
        error_msg.removeClass('hidden');
        error_msg.appendTo($('.posts'));
        $('#posts-loading-animation').fadeOut();
    }

    var async_event_count = 0;
    function loadPosts(){
        var event_number = ++async_event_count;
        $.ajax({
            url: '/feed/api/v1/posts/'+query,
            type: 'GET',
            error: function() { onFailure(); },
            success: function(result) {
                if( event_number >= async_event_count ) {
                    onSuccess(result);
                }
                --async_event_count;
            }
        });
    }

    var last_search_string = "";
    var search_event_count = 0;
    var typeWatchConfig = {
        callback: function (value) {
            var search_string = value;
            if( search_string != last_search_string ){
                last_search_string = search_string;
                $('#posts-loading-animation').fadeIn();
                clearPosts();
                query = "?count=5&q="+encodeURIComponent(search_string);
                loadPosts();
            }
        },
        wait: 750,
        highlight: true,
        captureLength: 2
    }
    $("#search-post-input").typeWatch(typeWatchConfig);

    $("#more-post-btn").click(function(){
        query += "&offset="+posts.length;
        $('#posts-loading-animation').fadeIn();
        loadPosts();
    });

    loadPosts();
    function csrfSafeMethod(method) {
        return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
    }
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
                xhr.setRequestHeader("X-CSRFToken", $('[name="csrfmiddlewaretoken"]').val());
            }
        }
    });

});
