var posts = [];

$(document).ready(function(){
    loadPosts();
});

function loadPosts(){
    $('#loading-animation').show();

    $.ajax({
        url: '/feed/api/v1/posts/?count=5',
        type: 'GET',
        error: function(){ console.log(':/'); },
        success: function(result){
            posts = result;
            setTimeout(function(){
                $('#loading-animation').hide();
                renderPosts();
            }, 1000);
        }
    });
}


function renderPosts(){
    var post_template = $('.post');
    var post_container = $('#posts');
    for( var i=0; i<posts.length; i++ ){
        var post = post_template.clone();
        post.find('h3').text(posts[i].title);
        post.find('author').text(posts[i].posted_by.first_name);
        post.find('time').attr('datetime', posts[i].posted_at);
        post.find('time').text($.timeago(posts[i].posted_at));
        post.find('p').text(posts[i].body);
        post.hide();
        post. appendTo(post_container);
        post.slideDown('1000');
    }
    $("article timeago").timeago();
}
