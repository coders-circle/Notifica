from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User

from classroom.models import *
from classroom.utils import *


class HomeView(TemplateView):
    def get(self, request):
        if isValidUser(request.user):
            return redirect("user")
        return render(request, "main/home.html")

class SigninView(View):
    def get(self, request):
        if isValidUser(request.user):
            return redirect("user")
        return render(request, "main/signin.html")

    def post(self, request):
        username = request.POST['username']
        password = request.POST['password']

        user = authenticate(username=username, password=password)
        if user and user.is_active:
            login(request, user)
            return redirect('user')

        context = {"error":"Invalid username or password."}
        return render(request, "main/signin.html", context)


class RegisterView(View):
    def get(self, request):
        return render(request, "main/register.html")

    def post(self, request):
        try:
            email = request.POST['email']
            username = request.POST['username']
            password = request.POST['password']

            if User.objects.filter(username=username).exists():
                raise ValueError("That username is already taken")

            user = User.objects.create_user(username, email, password)
            user.save()

            user = authenticate(username=username, password=password)
            login(request, user)
            return redirect('user')

        except Exception as e:
            context = {"error":str(e)}
            return render(request, "main/register.html", context)


# TODO: Replace user info with User class

class Comment(object):
    userName = None
    userProfileLink = "#"
    userAvatar = "ninja.png"
    content = None;
    time = "few seconds ago"

class UserPost(object):
    userName = None
    userProfileLink = "#"
    userAvatar = "ninja.png"
    content = None
    time = "few seconds ago";
    def __init__(self):
        self.comments = []


class UserView(View):
    def get(self, request):
        if isValidUser(request.user):
            testComment1 = Comment()
            testComment1.userName = "fhx"
            testComment1.content = "hello world"

            testComment2 = Comment()
            testComment2.userName = "Aditya Khatri"
            testComment2.content = "OK DOOD!"

            testComment3 = Comment()
            testComment3.userName = "Bibek Dahal"
            testComment3.content = "Test Comment"

            testPost1 = UserPost()
            testPost1.userName = "Absolute Zero";
            testPost1.content = "post post post";
            testPost1.time = "few minutes ago";
            testPost1.userAvatar = "fh.png"

            testPost2 = UserPost()
            testPost2.userName = "Aditya Khatri"
            testPost2.content = "OK DOOD!"
            testPost2.userAvatar = "ks.png"

            testPost1.comments.append(testComment1)
            testPost1.comments.append(testComment2)
            testPost2.comments.append(testComment3)

            userPosts = []
            userPosts.append(testPost1)
            userPosts.append(testPost2)

            context = {'userposts':userPosts}
            return render(request, "main/user.html", context)
        return redirect("home")

    def post(self, request):
        logout(request)
        return redirect("home")

