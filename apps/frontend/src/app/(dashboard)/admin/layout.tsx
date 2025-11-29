import { Navbar } from '@/components/common/Navbar';
import { AdminSidebar } from '@/components/admin/admin-sidebar';
import { AdminGuard } from '@/components/auth/role-guard';

/**
 * Admin 전용 레이아웃
 * - ADMIN 역할만 접근 가능
 * - 권한이 없으면 /403으로 리다이렉트
 */
export default function AdminLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AdminGuard>
      <div className="min-h-screen">
        <Navbar />
        <div className="flex">
          <AdminSidebar />
          <main className="ml-64 flex-1 p-8">{children}</main>
        </div>
      </div>
    </AdminGuard>
  );
}
