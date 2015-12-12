from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from classroom import views, rest_views


router = DefaultRouter()
router.register(r'organizations', rest_views.OrganizationViewSet)
router.register(r'departments', rest_views.DepartmentViewSet)
router.register(r'teachers', rest_views.TeacherViewSet)
router.register(r'subjects', rest_views.SubjectViewSet)
router.register(r'classes', rest_views.ClassViewSet)
router.register(r'groups', rest_views.GroupViewSet)
router.register(r'students', rest_views.StudentViewSet)
router.register(r'users', rest_views.UserViewSet)


urlpatterns = [
    
    url(r'^user/$', views.UserView.as_view(), name='user'),
    
    url(r'^api/v1/', include(router.urls)),
]
