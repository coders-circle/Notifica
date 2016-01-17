from django.conf.urls import url, include
from django.views.generic import RedirectView
from main import views


urlpatterns = [
    url(r'^$', RedirectView.as_view(url='/home/')),
    url(r'^home/$', views.HomeView.as_view(), name='home'),
    url(r'^signin/$', views.SigninView.as_view(), name='signin'),
    url(r'^signout/$', views.SignoutView.as_view(), name='signout'),
    url(r'^register/$', views.RegisterView.as_view(), name='register'),

    # url(r'^api/v1/', include('rest_framework.urls')),
]
