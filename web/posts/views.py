from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User

from classroom.models import *
from classroom.utils import *


# TODO: Replace user info with User class


class UserPost(object):
    userName = None
    userProfileLink = "#"
    userAvatar = "ninja.png"
    title = None
    content = None
    time = "few seconds ago";
    numComments = 0
    def __init__(self):
        self.tags = []


class FeedView(View):
    def get(self, request):
        if isValidUser(request.user):
            testPost1 = UserPost()
            testPost1.userName = "Absolute Zero"
            testPost1.title = "Test post, indeed"
            testPost1.userAvatar = "fh.png"
            testPost1.tags.append("awesome")
            testPost1.tags.append("test")
            testPost1.tags.append("cool")
            testPost1.tags.append("yolo")
            testPost1.numComments = 10

            testPost2 = UserPost()
            testPost2.userName = "Aditya Khatri"
            testPost2.title = "Test post returns"
            testPost2.userAvatar = "ks.png"
            testPost2.tags.append("noobness")
            testPost2.tags.append("lazy")

            testPost3 = UserPost()
            testPost3.userName = "Bibek Dahal"
            testPost3.title = "Rectify regex search error in REST api"
            testPost3.userAvatar = "ninja.png"
            testPost3.tags.append("commit")
            testPost3.tags.append("github")
            testPost3.tags.append("notifica")

            testPost4 = UserPost()
            testPost4.userName = "Bibek Dahal"
            testPost4.title = "Move user view to main app. Add some teachers"
            testPost4.userAvatar = "ninja.png"
            testPost4.tags.append("commit")
            testPost4.tags.append("github")
            testPost4.tags.append("notifica")

            testPost5 = UserPost()
            testPost5.userName = "Absolute Zero"
            testPost5.title = "fix a bug that caused object skipping in period processing"
            testPost5.userAvatar = "fh.png"
            testPost5.tags.append("commit")
            testPost5.tags.append("github")
            testPost5.tags.append("notifica")



            userPosts = []
            userPosts.append(testPost1)
            userPosts.append(testPost2)
            userPosts.append(testPost3)
            userPosts.append(testPost4)
            userPosts.append(testPost5)
            userPosts.append(testPost2)
            userPosts.append(testPost3)
            userPosts.append(testPost1)
            userPosts.append(testPost4)

            context = {'userposts':userPosts}
            return render(request, "posts/feed.html", context)
        return redirect("home")
