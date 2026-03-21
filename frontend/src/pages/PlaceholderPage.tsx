import { Layout } from '../components/Layout';
import { HeaderBar } from '../components/HeaderBar';

interface PlaceholderPageProps {
  title: string;
}

export function PlaceholderPage({ title }: PlaceholderPageProps) {
  return (
    <Layout>
      <HeaderBar title={title} showLive={false} searchPlaceholder="Search..." />
      <div
        style={{
          flex: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: 24,
        }}
      >
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 13,
            fontWeight: 500,
            color: 'var(--text-muted)',
          }}
        >
          Coming soon
        </span>
      </div>
    </Layout>
  );
}
