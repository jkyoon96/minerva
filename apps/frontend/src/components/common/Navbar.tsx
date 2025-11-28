import Link from 'next/link';
import { GraduationCap } from 'lucide-react';

export function Navbar() {
  return (
    <nav className="border-b">
      <div className="container flex h-16 items-center px-4">
        <Link href="/" className="flex items-center space-x-2">
          <GraduationCap className="h-6 w-6" />
          <span className="text-xl font-bold">EduForum</span>
        </Link>
      </div>
    </nav>
  );
}
