from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User

from classroom.models import *
from classroom.utils import *


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

        context = {"error":"Invalid username or password."}
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
            context = {"error":str(e)}
            return render(request, "main/register.html", context)


class SettingsView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        context = {}
        context["settings"] = "Settings"
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, "main/settings.html", context)
