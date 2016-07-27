$(document).ready(function(){
    $('body').on( 'click', '.tab', function(e){
        var current = $('#tabs .active');
        var target = $(this);
        var target_id = target.data('target');
        var current_id = current.data('target');
        if(target_id != current_id){
            var current_container = $('#tab-contents #'+ current_id);
            var target_container = $('#tab-contents #'+ target_id);
            current.removeClass('active');
            target.addClass('active');
            current_container.removeClass('active');
            current_container.hide();
            target_container.fadeIn();
            target_container.addClass('active');
        }
    });

    var teachers_table = $('#teachers-table').DataTable({
        paging: false,
        info: false,
        searching: false,
        ajax: {
            type: "GET",
            dataType: "json",
            dataSrc: '',
            url: "/classroom/api/v1/teachers/?class="+class_pk,
        },
        columns: [
            { data: "id"},
            {
                data: null,
                render: function(data, type, row){
                    if(data.user){
                        return data.user.first_name+' '+data.user.last_name;
                    } else{
                        return data.username;
                    }
                }
            },
            {
                data: null,
                render: function(data, type, row){
                    if(data.user){
                        return data.user.email;
                    }
                    else {
                        return '';
                    }
                }
            }
        ]
    });

    var students_table = $('#students-table').DataTable({
        paging: false,
        info: false,
        searching: false,
        ajax: {
            type: "GET",
            dataType: "json",
            dataSrc: '',
            url: "/classroom/api/v1/students/?class="+class_pk,
        },
        columns: [
            { data: "id"},
            {
                data: null,
                render: function(data, type, row){
                    if(data.user){
                        return data.user.first_name+' '+data.user.last_name;
                    } else{
                        return data.username;
                    }
                }
            },
            {
                data: null,
                render: function(data, type, row){
                    if(data.user){
                        return data.user.email;
                    }
                    else {
                        return '';
                    }
                }
            }
        ]
    });

    var subjects_table = $('#subjects-table').DataTable({
        paging: false,
        info: false,
        searching: false,
        ajax: {
            type: "GET",
            dataType: "json",
            dataSrc: '',
            url: "/classroom/api/v1/subjects/?class="+class_pk,
        },
        columns: [
            { data: "id"},
            {data: 'name'},
        ]
    });



    $("#add-post-form-container .header").click(function(e){
        var container = $(this).parent();
        var expanded =  container.data('expanded');
        if(expanded){
            container.data('expanded', false);
            container.find('form').slideUp();
            container.removeClass('expanded');
            var label =  $(this).find('.header-label');
            label.animate({
                right: 0,
                fontSize: '1em',
            });
        } else {
            container.data('expanded', true);
            container.addClass('expanded');
            container.find('form').slideDown();
            var label =  $(this).find('.header-label');
            label.animate({
                right: $(this).width() - label.width() - 24,
                fontSize: '0.88em',
            });
        }
    });

    $("#add-post-form").submit(function(e){
        e.preventDefault();
        if($("#post-title").val().length == 0){
            $("#post-title").focus();
        } else if($("#post-content").val().length == 0){
            $("#post-content").focus();
        } else{
            addPost();
        }
    });

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



    $('#add-post-container header').on('click', 'a', function(){
        $(this).animate({ 'left': '0' }, function(){ $(this).css('right', 'unset');});
        $('#add-post-container form').slideDown();
    });
    $('#add-post-container form').on('click', 'a', function(){
        $('#add-post-container header a').css('left', 'unset' );
        $('#add-post-container header a').animate({ 'right': '0'});
        $('#add-post-container form').slideUp();
    });


    var posts = [];

    function loadPosts(){
        $.ajax({
            url: '/feed/api/v1/posts/?count=5',
            type: 'GET',
            error: function(){ console.log(':/'); },
            success: function(result){
                posts = result;
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

    loadPosts();

});
