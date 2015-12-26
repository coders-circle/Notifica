from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, logout

from classroom.models import *
from classroom.utils import *
