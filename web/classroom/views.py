from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
from django.http import Http404, JsonResponse

from classroom.models import *
from main.models import *
from classroom.utils import *
from main.decorators import *


class UserView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/user.html", context)


class ClassView(View):
    def is_user_in_class(self, request, id):
        try:
            students = Student.objects.filter(user__pk=request.user.pk)
            for s in students:
                if s.group.p_class.pk == int(id):
                    return True
            return False
        except:
            return False

    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["class"] = Class.objects.get(pk=id)
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile

        # Check if user is in class and if not
        # check if join request has already been sent
        in_class = self.is_user_in_class(request, id)
        context["user_in_class"] = in_class
        requests = Request.objects.filter(sender=request.user.pk,
                                          sender_type=0, status=0,
                                          request_type=0, to=id)

        if not in_class and requests.count() > 0:
                context["request_sent"] = True

        return render(request, "classroom/class.html", context)

    def post(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        in_class = self.is_user_in_class(request, id)
        if not in_class and request.POST.get("join-request"):

            # Send a join request if not already sent
            if Request.objects.filter(sender=request.user.pk,
                                      sender_type=0, status=0, request_type=0,
                                      to=id).count() == 0:
                new_request = Request()
                new_request.sender = request.user.pk
                new_request.sender_type = 0
                new_request.status = 0
                new_request.request_type = 0
                new_request.to = id
                new_request.save()
        return self.get(request, id)


class DepartmentView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["department"] = Department.objects.get(pk=id)
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/department.html", context)


class OrganizationView(View):
    def get(self, request, id):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["organization"] = Organization.objects.get(pk=id)
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/organization.html", context)


class SearchView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/search.html", context)


class AddClassView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        request.user.profile = \
            UserProfile.objects.get(user=request.user).profile
        return render(request, "classroom/add-class.html", context)

    def post(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        try:
            class_name = request.POST['class-name']
            description = request.POST['description']

            new_class = Class(class_id=class_name, description=description)
            new_profile = Profile()
            new_profile.save()
            new_class.profile = new_profile
            new_class.save()
            new_class.admins.add(request.user)

            return redirect('classroom:class', id=new_class.pk)

        except Exception as e:
            context = {"error": str(e)}
            return render(request, "classroom/add-class.html", context)


class SelectElectiveView(View):

    @method_decorator(csrf_exempt)
    @method_decorator(basicauth)
    def dispatch(self, *args, **kwargs):
        return super(SelectElectiveView, self).dispatch(*args, **kwargs)

    def get(self, request, id=-1):
        Http404("Invalid page for GET request")

    def post(self, request, id):
        if not isValidUser(request.user):
            raise Http404("Invalid user")

        students = Student.objects.filter(user__pk=self.request.user.pk)
        elective = Elective.objects.get(pk=id)

        # Remove student from all other electives in same group
        others = Elective.objects.filter(p_class__pk=elective.p_class.pk,
                                         group=elective.group)

        for e in others:
            for s in students:
                if s in e.students.all():
                    e.students.remove(s)

        # Select student for this elective
        for s in students:
            if s not in elective.students.all():
                elective.students.add(s)

        return JsonResponse({"result": "success"})
