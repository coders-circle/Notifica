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


class ClassView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")
        
        p_class = Class.objects.filter(pk=id)
        context = {"user":request.user, "class":p_class}
        return render(request, "classroom/class.html", context)


class DepartmentView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")
        
        department = Department.objects.filter(pk=id)
        context = {"user":request.user, "department":department}
        return render(request, "classroom/department.html", context)


class OrganizationView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")
        
        organization = Organization.objects.filter(pk=id)
        context = {"user":request.user, "class":organization}
        return render(request, "classroom/organization.html", context)
