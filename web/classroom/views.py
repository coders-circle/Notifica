from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout


class UserView(TemplateView):

    def get(self, request):
        if request.user.is_authenticated and not request.user.is_superuser:
            return render(request, "classroom/user.html")
        return redirect("home")

    def post(self, request):
        logout(request)
        return redirect("home")
