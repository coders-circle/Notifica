from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout

from classroom.models import *
from classroom.users import *


class UserView(View):

    def get(self, request):
        if isValidUser(request.user):
            return render(request, "classroom/user.html")
        return redirect("home")

    def post(self, request):
        logout(request)
        return redirect("home")
