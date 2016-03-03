from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.http import JsonResponse

from classroom.models import *
from classroom.utils import *
from main.models import *
from main.utils import *
from main.decorators import *
from main.notify import *


class HomeView(TemplateView):
    def get(self, request):
        if isValidUser(request.user):
            return redirect("posts:feed")
        return render(request, "main/home.html")


class SigninView(View):
    def get(self, request):
        if isValidUser(request.user):
            return redirect("posts:feed")
        return render(request, "main/signin.html")

    def post(self, request):
        username = request.POST['username']
        password = request.POST['password']

        user = authenticate(username=username, password=password)
        if user and user.is_active:
            login(request, user)
            return redirect('posts:feed')

        context = {"error": "Invalid username or password."}
        return render(request, "main/signin.html", context)


class SignoutView(View):
    def get(self, request):
        logout(request)
        return redirect("home")


class RegisterView(View):
    def get(self, request):
        return render(request, "main/register.html")

    def post(self, request):
        try:
            email = request.POST['email']
            username = request.POST['username']
            password = request.POST['password']
            first_name = request.POST['first-name']
            last_name = request.POST['last-name']

            if User.objects.filter(username=username).exists():
                raise ValueError("That username is already taken")

            user = User.objects.create_user(username, email, password)
            user.first_name = first_name
            user.last_name = last_name
            user.save()

            user = authenticate(username=username, password=password)
            login(request, user)
            return redirect('posts:feed')

        except Exception as e:
            context = {"error": str(e)}
            return render(request, "main/register.html", context)


class SettingsView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["settings"] = "Settings"
        request.user.profile = UserProfile.objects.get(
            user=request.user).profile
        return render(request, "main/settings.html", context)


class NotifyView(View):
    def get(self, request):
        if not request.user or not request.user.is_superuser:
            return redirect("home")

        context = {}
        context["users"] = User.objects.filter(
            pk__in=GcmRegistration.objects.all().values_list(
                'user', flat=True))
        request.user.profile = UserProfile.objects.get(
            user=request.user).profile
        return render(request, "main/notify.html", context)

    def post(self, request):
        context = {}
        context["users"] = User.objects.filter(
            pk__in=GcmRegistration.objects.all().values_list(
                'user', flat=True))
        request.user.profile = UserProfile.objects.get(
            user=request.user).profile

        try:
            title = request.POST['title']
            message = request.POST['message']

            user = request.POST['user']
            tokens = GcmRegistration.objects.filter(
                user__pk=user).values_list('token', flat=True)

            notify(tokens, title, message)

        except Exception as e:
            context["error"] = str(e)

        return render(request, "main/notify.html", context)


class RequestResponseView(View):
    def get(self, request, id=-1):
        raise Http404("Invalid page for GET request")

    def post(self, request, id, status):
        if not isValidUser(request.user):
            raise Http404("Invalid user")

        status = int(status)

        req = Request.objects.get(pk=id)

        # TODO Check if request can be handled by the user
        # TODO Validate status code

        req.status = status
        req.save()

        try:
            input = json.loads(request.body.decode("utf-8"))
        except:
            input = request.POST

        if status == 1 and req.request_type == 0:
            if "group" in input:
                p_group = Group.objects.get(pk=input["group"])
            else:
                p_group = Group.objects.get(p_class__pk=req.to)

            student = Student()
            student.user = request.user
            student.group = p_group
            student.save()

        # TODO Roll number
        return JsonResponse({"detail": "success"})
