from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User

from classroom.models import *
from classroom.utils import *
from posts.models import *
from main.utils import *


class FeedView(View):
    def get(self, request):
        if isValidUser(request.user):
            context = {}
            context["current_page"] = "News feed"

            classes = Class.objects.filter(pk__in=get_classes(request.user))
            context["classes"] = classes
            notifications = get_notifications(request.user)
            if len(notifications) > 0:
                context["notifications"] = notifications

            departments = Department.objects.filter(
                pk__in=get_departments(request.user))
            context["departments"] = departments

            request.user.profile = UserProfile.objects.get(
                user=request.user).profile
            return render(request, "posts/feed.html", context)
        return redirect("home")


class PostView(View):
    def get(self, request, post_id):
        if isValidUser(request.user):
            context = {}
            context["post"] = Post.objects.get(pk=post_id)
            request.user.profile = UserProfile.objects.get(
                user=request.user).profile
            return render(request, "posts/post.html", context)
        return redirect("home")
