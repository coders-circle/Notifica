from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from routine import views, rest_views


router = DefaultRouter()
router.register(r'routines', rest_views.RoutineViewSet)
router.register(r'periods', rest_views.PeriodViewSet, base_name='period')


urlpatterns = [
    url(r'^$', views.RoutineView.as_view(), name="routine"),
    url(r'^admin/$', views.RoutineAdminView.as_view(), name="admin"),
    url(r'^api/v1/', include(router.urls)),
]
