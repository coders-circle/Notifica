from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout

from classroom.models import *
from classroom.utils import *


class UserView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/user.html", context)


class ClassView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["class"] = Class.objects.get(pk=id)
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/class.html", context)


class AddClassView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/add-class.html", context)


class DepartmentView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["department"] = Department.objects.get(pk=id)
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/department.html", context)


class OrganizationView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["organization"] = Organization.objects.get(pk=id)
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/organization.html", context)


class SearchView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/search.html", context)
