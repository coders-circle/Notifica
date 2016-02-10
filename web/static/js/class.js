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

    loadTeachers();
    loadStudents();
    loadSubjects();


    function loadTeachers(){
        $.ajax({
            url: '/classroom/api/v1/teachers',
            type: 'GET',
            error: function() {
                alert('failed to fetch teachers :/');
            },
            success: function(res) {
                if(res.length > 0){
                    var evenContainer = $('#teachers').find('.even-container');
                    var oddContainer = $('#teachers').find('.odd-container');
                    var teacherCardTemplate = $('.template-card-teacher');
                    for(i=0; i<res.length; i++){
                        var teacherCard = teacherCardTemplate.clone();
                        if(res[i].avatar)
                            teacherCard.find('.avatar').attr('src', res[i].avatar);
                        if(res[i].user){
                            teacherCard.find('.name').text(res[i].user.first_name + ' ' + res[i].user.last_name);
                            teacherCard.find('.extra').text(res[i].user.email);
                        } else {
                            teacherCard.find('.name').text(res[i].username);
                        }

                        teacherCard.removeClass('card-teacher-template');
                        teacherCard.addClass('card-teacher');
                        if(i%2 == 0){
                            teacherCard.appendTo(evenContainer);
                        } else{
                            teacherCard.appendTo(oddContainer);
                        }
                        teacherCard.removeClass('hidden');
                    }
                }
            }
        });
    }

    function loadStudents(){
        $.ajax({
            url: '/classroom/api/v1/students',
            type: 'GET',
            error: function() {
                alert('failed to fetch students :/');
            },
            success: function(res) {
                if(res.length > 0){
                    var container = $('.table-students');
                    var headerRow = $('<tr><th>S.N.</th><th>Name</th><th>Roll</th></tr>');
                    var rowTemplate = $('<tr></tr>');
                    var dataTemplate = $('<td></td>');
                    headerRow.appendTo(container);
                    for(i=0; i<res.length; i++){
                        var row = rowTemplate.clone();
                        var sn = dataTemplate.clone();
                        var name = dataTemplate.clone();
                        var roll = dataTemplate.clone();
                        if(res[i].user){
                            name.text(res[i].user.first_name + ' ' + res[i].user.last_name);
                        } else{
                            name.text(res[i].username);
                        }
                        sn.text(''+(i+1));
                        roll.text('069-BCT-xxx');
                        sn.appendTo(row);
                        name.appendTo(row);
                        roll.appendTo(row);
                        row.appendTo(container);
                    }
                }
            }
        });
    }

    function loadSubjects(){
        $.ajax({
            url: '/classroom/api/v1/subjects',
            type: 'GET',
            error: function() {
                alert('failed to fetch subjects :/');
            },
            success: function(res) {
                if(res.length > 0){
                    var evenContainer = $('#subjects').find('.even-container');
                    var oddContainer = $('#subjects').find('.odd-container');
                    var subjectTemplate = $('.template-card-subject');
                    for(i=0; i<res.length; i++){
                        var subject = subjectTemplate.clone();
                        subject.find('.shortname').text(res[i].short_name);
                        subject.find('.shortname').css('background-color', res[i].color);
                        subject.find('.name').text(res[i].name);
                        subject.find('.extra').text("Department of Electronics & Computer Engineering");
                        subject.removeClass('template-card-subject');
                        subject.addClass('card-subject');
                        if(i%2 == 0){
                            subject.appendTo(evenContainer);
                        } else{
                            subject.appendTo(oddContainer);
                        }
                        subject.removeClass('hidden');
                    }
                }
            }
        });
    }
});
