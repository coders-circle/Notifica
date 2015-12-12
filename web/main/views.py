from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User


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
            return redirect('classroom:user')

        except Exception as e:
            context = {"error":str(e)}
            return render(request, "main/register.html", context)
