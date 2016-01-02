jQuery(document).ready(function($) {
    var posts = [];
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
        }
    }
    function renderPosts(ajaxRes){
        var posts_container = $('#posts');
        var post_template = $('.template-userpost').clone();
        var tag_template = $('<span class="tag"></span>');
        for(var i = 0; i < ajaxRes.length; i++){
            var post = post_template.clone();
            post.find('.num').text('10');
            post.find('.user-avatar').attr('src', '/static/img/ninja.png');
            post.find('.title').text(ajaxRes[i].title);
            post.find('.user-name').text(ajaxRes[i].posted_by);
            var posted_on = new Date(ajaxRes[i].posted_on);
            post.find('.time').text(posted_on.toLocaleString());
            var tags_container = post.find('.tags');
                if(ajaxRes[i].tags){
                var tags = $.parseJSON(ajaxRes[i].tags);
                for(var j = 0; j < tags.length; j++){
                    var tag = tag_template.clone();
                    tag.text(tags[j]);
                    tag.appendTo(tags_container);
                }
            }
            post.removeClass('template-userpost');
            post.removeClass('hidden');
            post.addClass('userpost');
            post.appendTo(posts_container);
            if(i != ajaxRes.length-1){
                posts_container.append('<hr>');
            }
        }
        $('#msg-empty').hide();
        $('#posts-loading-animation').fadeOut();
    }
    $("#sidebar-toggle").click(function(e) {
        $("#wrapper").toggleClass("toggled");
    });

    $.ajax({
        url: '/feed/api/v1/posts/',
        type: 'GET',
        error: function() {
            setTimeout(function(){
                $('#posts-loading-animation').fadeOut();
                console.log('here');
            }, 2000);
        },
        success: function(res) {
            if(res.length > 0){
                setTimeout(function(){
                    renderPosts(res);
                }, 2000);
            }else{
                setTimeout(function(){
                    $('#posts-loading-animation').fadeOut();
                }, 2000);
            }

        }
    });

    var last_search_str = "";
    $("#search").keyup(function(){
        var search_str = $("#search").val();
        if( search_str != last_search_str){
            last_search_str = search_str;
            typewatch(function(){
                removeOldPosts();
                $('#msg-empty').show();
                $('#posts-loading-animation').fadeIn();
                $.ajax({
                    url: '/feed/api/v1/posts/?q='+$("#search").val(),
                    type: 'GET',
                    error: function() {
                        setTimeout(function(){
                            $('#posts-loading-animation').fadeOut();
                            console.log('here');
                        }, 500);
                    },
                    success: function(res) {
                        if(res.length > 0){
                            setTimeout(function(){
                                renderPosts(res);
                            }, 500);
                        }else{
                            setTimeout(function(){
                                $('#posts-loading-animation').fadeOut();
                            }, 500);
                        }

                    }
                });
            }, 500);
        }
    });
});
