from django.shortcuts import render
from django.views.generic import View, TemplateView


class RoutineView(TemplateView):
    template_name = "routine/routine.html"
