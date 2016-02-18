from django.conf.urls import url, include
from django.views.generic import RedirectView
from rest_framework.routers import DefaultRouter
from main import views, rest_views


router = DefaultRouter()
router.register(r'requests', rest_views.RequestViewSet, base_name='request')
router.register(r'gcm-registrations', rest_views.GcmRegistrationViewSet, base_name='gcm_registration')


urlpatterns = [
    url(r'^$', RedirectView.as_view(url='/home/')),
    url(r'^home/$', views.HomeView.as_view(), name='home'),
    url(r'^signin/$', views.SigninView.as_view(), name='signin'),
    url(r'^signout/$', views.SignoutView.as_view(), name='signout'),
    url(r'^register/$', views.RegisterView.as_view(), name='register'),

    url(r'^settings/$', views.SettingsView.as_view(), name='settings'),

    url(r'^api/v1/', include(router.urls)),
]
