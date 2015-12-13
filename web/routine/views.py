from django.shortcuts import render
from django.views.generic import View, TemplateView
from django.db.models import Q

from classroom.models import *
from routine.models import *
from routine.utils import *


days = ["Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"]


class RoutineView(View):

    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        r, s, e = getRoutine(request.user)
        context = {"days": days, "routine": r, "start_time": s, "end_time": e}
        return render(request, 'routine/routine.html', context)

    # def post(self, request):
    #
