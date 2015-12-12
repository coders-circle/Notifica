from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from routine import views, rest_views


router = DefaultRouter()
router.register(r'routines', rest_views.RoutineViewSet)
router.register(r'periods', rest_views.PeriodViewSet)


urlpatterns = [
    url(r'^routine/', views.RoutineView.as_view(), name="routine"),
    url(r'^rest/', include(router.urls)),
]
