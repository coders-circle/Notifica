from django.shortcuts import render
from django.views.generic import View, TemplateView
from django.db.models import Q

from classroom.models import *
from classroom.views import *
from routine.models import *


days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]

class RoutineView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        usertype = getUserType(request.user)

        context = {}

        periods = []
        if usertype == "Student":
            s = request.user.student
            periods = Period.objects.filter(Q(groups=s.group)|Q(routine__p_class=s.group.p_class)).distinct()
        elif usertype == "Teacher":
            t = request.user.teacher
            periods = Period.objects.filter(teachers=t).distinct()
        
        context["routine"] = {}

        for d in range(7):
            context["routine"][d] = []

        for p in periods:
            context["routine"][p.day].append(p)

        context["days"] = days

        return render(request, 'routine/routine.html', context)
