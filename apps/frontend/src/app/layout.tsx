import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import '@/styles/globals.css';
import { Providers } from '@/lib/providers';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: {
    default: 'EduForum - 대화형 온라인 학습 플랫폼',
    template: '%s | EduForum',
  },
  description: '미네르바 대학의 Active Learning Forum을 참고한 교육 플랫폼',
  keywords: ['교육', '온라인 학습', '화상 세미나', 'LMS', '액티브 러닝'],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <body className={inter.className}>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
