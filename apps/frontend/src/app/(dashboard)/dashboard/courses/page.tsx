import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Plus, BookOpen } from 'lucide-react';

export default function CoursesPage() {
  // 임시 데이터 (추후 API에서 가져올 예정)
  const courses = [
    {
      id: '1',
      title: '자료구조와 알고리즘',
      code: 'CS201',
      semester: '2025 Spring',
      professor: '김교수',
    },
    {
      id: '2',
      title: '데이터베이스 시스템',
      code: 'CS301',
      semester: '2025 Spring',
      professor: '이교수',
    },
    {
      id: '3',
      title: '웹 프로그래밍',
      code: 'CS202',
      semester: '2025 Spring',
      professor: '박교수',
    },
  ];

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">내 코스</h1>
          <p className="text-muted-foreground">수강 중인 코스 목록</p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          코스 등록
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {courses.map((course) => (
          <Card key={course.id} className="hover:shadow-lg transition-shadow cursor-pointer">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                  <BookOpen className="h-6 w-6" />
                </div>
                <span className="rounded-full bg-secondary px-3 py-1 text-xs font-medium">
                  {course.code}
                </span>
              </div>
              <CardTitle className="mt-4">{course.title}</CardTitle>
              <CardDescription>
                {course.professor} • {course.semester}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button variant="outline" className="w-full">
                코스 보기
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
