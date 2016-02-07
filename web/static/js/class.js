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
            current_container.addClass('hidden');
            target_container.removeClass('hidden');
            target_container.addClass('active');
        }
    });

    function loadTeachers(){
        $.ajax({
            url: '/classroom/api/v1/teachers',
            type: 'GET',
            error: function() {
                alert('failed to fetch teacher info :/');
            },
            success: function(res) {
                
            }
        });
    }
});
