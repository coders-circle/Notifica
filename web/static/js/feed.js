var posts = [];

$(document).ready(function(){
    loadPosts();
});

function loadPosts(){
    $('#loading-animation').show();

    $.ajax({
        url: '/feed/api/v1/posts/?count=10',
        type: 'GET',
        error: function(){ console.log(':/'); },
        success: function(result){
            posts = result;
            $('#loading-animation').hide();
            renderPosts();
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

String.prototype.trunc = function( n, useWordBoundary=true ){
    var isTooLong = this.length > n,
    s_ = isTooLong ? this.substr(0,n-1) : this;
    s_ = (useWordBoundary && isTooLong) ? s_.substr(0,s_.lastIndexOf(' ')) : s_;
    return  isTooLong ? s_ + '...' : s_;
};

function acceptRequest(id){
    $.ajax({
        url: '/request-response/'+id+'/1/',
        type: 'POST',
        data: {
            // "group" : xx,
            // "roll" : yy,
        },
        success: function(e){
            alert("successful!");
            $("#request-"+id).remove();
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}

function rejectRequest(id){
    $.ajax({
        url: '/request-response/'+id+'/2/',
        type: 'POST',
        success: function(e){
            alert("successful!");
            $("#request-"+id).remove();
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}
