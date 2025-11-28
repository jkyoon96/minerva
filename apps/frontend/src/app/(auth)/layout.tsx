import { GraduationCap } from 'lucide-react';
import Link from 'next/link';

export default function AuthLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-background to-secondary/20">
      <Link href="/" className="mb-8 flex items-center space-x-2">
        <GraduationCap className="h-8 w-8" />
        <span className="text-2xl font-bold">EduForum</span>
      </Link>
      {children}
    </div>
  );
}
