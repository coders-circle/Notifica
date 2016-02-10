from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from classroom import views, rest_views


router = DefaultRouter()
router.register(r'profiles', rest_views.ProfileViewSet, base_name='profile')
router.register(r'organizations', rest_views.OrganizationViewSet, base_name='organization')
router.register(r'departments', rest_views.DepartmentViewSet, base_name='department')
router.register(r'teachers', rest_views.TeacherViewSet, base_name='teacher')
router.register(r'subjects', rest_views.SubjectViewSet, base_name='subject')
router.register(r'classes', rest_views.ClassViewSet, base_name='class')
router.register(r'groups', rest_views.GroupViewSet, base_name='group')
router.register(r'students', rest_views.StudentViewSet, base_name='student')
router.register(r'users', rest_views.UserViewSet, base_name='user')
router.register(r'requests', rest_views.RequestViewSet, base_name='request')


urlpatterns = [
    url(r'^user/$', views.UserView.as_view(), name='user'),
    url(r'^class/(?P<id>\d+)/$', views.ClassView.as_view(), name='class'),
    url(r'^department/(?P<id>\d+)/$', views.DepartmentView.as_view(), name='department'),
    url(r'^organization/(?P<id>\d+)/$', views.OrganizationView.as_view(), name='organization'),

    url(r'^api/v1/', include(router.urls)),
]
