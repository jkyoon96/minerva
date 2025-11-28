import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

export default function RegisterPage() {
  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>회원가입</CardTitle>
        <CardDescription>새로운 계정을 만들어 EduForum을 시작하세요</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4">
          <div className="space-y-2">
            <label htmlFor="name" className="text-sm font-medium">
              이름
            </label>
            <Input id="name" type="text" placeholder="홍길동" required />
          </div>
          <div className="space-y-2">
            <label htmlFor="email" className="text-sm font-medium">
              이메일
            </label>
            <Input id="email" type="email" placeholder="name@example.com" required />
          </div>
          <div className="space-y-2">
            <label htmlFor="password" className="text-sm font-medium">
              비밀번호
            </label>
            <Input id="password" type="password" required />
          </div>
          <div className="space-y-2">
            <label htmlFor="confirmPassword" className="text-sm font-medium">
              비밀번호 확인
            </label>
            <Input id="confirmPassword" type="password" required />
          </div>
          <div className="space-y-2">
            <label htmlFor="role" className="text-sm font-medium">
              역할
            </label>
            <select
              id="role"
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              required
            >
              <option value="student">학생</option>
              <option value="professor">교수</option>
              <option value="ta">조교</option>
            </select>
          </div>
          <Button type="submit" className="w-full">
            회원가입
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          이미 계정이 있으신가요?{' '}
          <Link href="/login" className="text-primary hover:underline">
            로그인
          </Link>
        </p>
      </CardContent>
    </Card>
  );
}
