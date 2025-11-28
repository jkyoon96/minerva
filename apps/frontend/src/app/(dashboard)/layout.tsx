import { Navbar } from '@/components/common/Navbar';
import { Sidebar } from '@/components/common/Sidebar';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="ml-64 flex-1 p-8">{children}</main>
      </div>
    </div>
  );
}
