from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.db.models import Q

import json

from classroom.models import *
from classroom.utils import *
from routine.models import *
from routine.utils import *


days = ["Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"]
days_short = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]


class RoutineView(View):

    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        r, s, e = getRoutine(request.user)

        context = {"days_short": days_short, "days": days, "routine": r, "start_time": s, "end_time": e}

        student = getStudent(request.user)
        if student:
            groups = Group.objects.filter(p_class.pk=student.group.p_class.pk)
            context["groups"] = groups

        return render(request, 'routine/routine.html', context)

    def post(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        # student = getStudent(request.user)
        # if student:
        #     Period.objects.all().delete()
        #     routine = json.loads(request.POST.get('routine'))
        #     r = Routine.objects.get(p_class.pk=student.group.p_class.pk)

        #     for d, day in enumerate(routine):
        #         for period in day:
        #             p = Period()
        #             p.routine = r
        #             p.subject = getSubject(period.subject)
        #             p.teachers = getTeachers(period.teachers)
        #             p.start_time = period.start_time
        #             p.end_time = period.end_time
        #             p.day = d
        #             p.remarts = period.remarks
        #             p.groups = getGroups(period.groups)
        #             p.save()

        return self.get(request)
