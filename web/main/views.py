from django.shortcuts import render
from django.views.generic import TemplateView


class HomeView(TemplateView):
    template_name = "main/home.html"

class SigninView(TemplateView):
    template_name = "main/signin.html"

class RegisterView(TemplateView):
    template_name = "main/register.html"
