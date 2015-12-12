from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login


class HomeView(TemplateView):
    template_name = "main/home.html"

class SigninView(View):
    def get(self, request):
        return render(request, "main/signin.html")

    def post(self, request):
        username = request.POST['username']
        password = request.POST['password']

        user = authenticate(username=username, password=password)
        if user and user.is_active:
            login(request, user)
            return redirect('classroom:user')
        return render(request, "main/signin.html")


class RegisterView(TemplateView):
    template_name = "main/register.html"
