from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout

from classroom.models import *
from classroom.utils import *


class UserView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {"user":request.user}
        return render(request, "classroom/user.html", context)
