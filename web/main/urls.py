from django.conf.urls import url
from django.views.generic import RedirectView
from main import views


urlpatterns = [
    url(r'^$', RedirectView.as_view(url='/home/')),
    url(r'^home/$', views.HomeView.as_view(), name='home'),
    url(r'^signin/$', views.SigninView.as_view(), name='signin'),
    url(r'^register/$', views.RegisterView.as_view(), name='register'),

    url(r'^user/$', views.UserView.as_view(), name='user'),
]
