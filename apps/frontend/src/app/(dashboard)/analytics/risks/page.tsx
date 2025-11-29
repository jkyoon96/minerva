'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import analyticsApi from '@/lib/api/analytics';
import { RiskStudentList } from '@/components/analytics/risk/risk-student-list';
import { RiskIndicator } from '@/components/analytics/risk/risk-indicator';
import { AlertCard } from '@/components/analytics/risk/alert-card';
import { InterventionModal } from '@/components/analytics/risk/intervention-modal';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { useToast } from '@/hooks/use-toast';
import { RiskLevel, AlertStatus } from '@/types/analytics';
import { Settings } from 'lucide-react';

export default function RisksPage() {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState('overview');
  const [selectedStudent, setSelectedStudent] = useState<number | null>(null);
  const [showInterventionModal, setShowInterventionModal] = useState(false);

  // Mock course ID
  const courseId = 1;

  // Fetch risk students
  const { data: riskStudents = [] } = useQuery({
    queryKey: ['analytics', 'risks', courseId],
    queryFn: () => analyticsApi.risks.getRiskStudents({ courseId }),
  });

  // Fetch alerts
  const { data: alerts = [] } = useQuery({
    queryKey: ['analytics', 'alerts', courseId],
    queryFn: () => analyticsApi.risks.getAlerts({ courseId, status: AlertStatus.ACTIVE }),
  });

  // Fetch alert settings
  const { data: settings } = useQuery({
    queryKey: ['analytics', 'alert-settings', courseId],
    queryFn: () => analyticsApi.risks.getAlertSettings(courseId),
  });

  // Mutations
  const acknowledgeAlert = useMutation({
    mutationFn: (alertId: number) =>
      analyticsApi.risks.acknowledgeAlert({ alertId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['analytics', 'alerts'] });
      toast({ title: 'Alert acknowledged' });
    },
  });

  const resolveAlert = useMutation({
    mutationFn: (data: { alertId: number; resolution: string }) =>
      analyticsApi.risks.resolveAlert(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['analytics', 'alerts'] });
      toast({ title: 'Alert resolved', variant: 'default' });
    },
  });

  const createIntervention = useMutation({
    mutationFn: analyticsApi.risks.createIntervention,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['analytics', 'risks'] });
      toast({ title: 'Intervention recorded' });
      setShowInterventionModal(false);
    },
  });

  // Calculate risk distribution
  const riskDistribution = {
    [RiskLevel.CRITICAL]: riskStudents.filter((s) => s.riskLevel === RiskLevel.CRITICAL).length,
    [RiskLevel.HIGH]: riskStudents.filter((s) => s.riskLevel === RiskLevel.HIGH).length,
    [RiskLevel.MEDIUM]: riskStudents.filter((s) => s.riskLevel === RiskLevel.MEDIUM).length,
    [RiskLevel.LOW]: riskStudents.filter((s) => s.riskLevel === RiskLevel.LOW).length,
    [RiskLevel.NONE]:
      riskStudents.length -
      riskStudents.filter(
        (s) =>
          s.riskLevel === RiskLevel.CRITICAL ||
          s.riskLevel === RiskLevel.HIGH ||
          s.riskLevel === RiskLevel.MEDIUM ||
          s.riskLevel === RiskLevel.LOW
      ).length,
  };

  const handleAddIntervention = (studentId: number) => {
    setSelectedStudent(studentId);
    setShowInterventionModal(true);
  };

  const handleResolveAlert = (alertId: number) => {
    resolveAlert.mutate({
      alertId,
      resolution: 'Resolved by instructor',
    });
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Risk Management</h1>
        <p className="text-muted-foreground">Early warning system and student interventions</p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="students">At-Risk Students</TabsTrigger>
          <TabsTrigger value="alerts">Alerts</TabsTrigger>
          <TabsTrigger value="settings">Settings</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="mt-6 space-y-6">
          {/* Risk Indicators */}
          <div className="grid gap-4 md:grid-cols-3">
            <RiskIndicator
              level={RiskLevel.CRITICAL}
              count={riskDistribution[RiskLevel.CRITICAL] + riskDistribution[RiskLevel.HIGH]}
              label="High Risk"
              description="Immediate attention needed"
              onClick={() => setActiveTab('students')}
            />
            <RiskIndicator
              level={RiskLevel.MEDIUM}
              count={riskDistribution[RiskLevel.MEDIUM]}
              label="Medium Risk"
              description="Monitor closely"
              onClick={() => setActiveTab('students')}
            />
            <RiskIndicator
              level={RiskLevel.NONE}
              count={riskDistribution[RiskLevel.NONE]}
              label="Normal"
              description="No concerns"
            />
          </div>

          {/* Recent Alerts */}
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>Recent Alerts</CardTitle>
                <Button variant="outline" size="sm" onClick={() => setActiveTab('alerts')}>
                  View All
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {alerts.slice(0, 3).map((alert) => (
                <AlertCard
                  key={alert.id}
                  alert={alert}
                  onAcknowledge={(id) => acknowledgeAlert.mutate(id)}
                  onResolve={handleResolveAlert}
                />
              ))}
              {alerts.length === 0 && (
                <p className="text-center text-sm text-muted-foreground">No active alerts</p>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="students" className="mt-6">
          <RiskStudentList
            students={riskStudents.filter(
              (s) => s.riskLevel === RiskLevel.HIGH || s.riskLevel === RiskLevel.CRITICAL
            )}
            onStudentClick={(id) => console.log('View student', id)}
            onEmailClick={(id) => console.log('Email student', id)}
            onAddNote={handleAddIntervention}
          />
        </TabsContent>

        <TabsContent value="alerts" className="mt-6 space-y-4">
          {alerts.map((alert) => (
            <AlertCard
              key={alert.id}
              alert={alert}
              onAcknowledge={(id) => acknowledgeAlert.mutate(id)}
              onResolve={handleResolveAlert}
            />
          ))}
        </TabsContent>

        <TabsContent value="settings" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Settings className="h-5 w-5" />
                Alert Configuration
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-4">
                <h3 className="font-semibold">Alert Thresholds</h3>
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label>Consecutive Absences</Label>
                    <Input
                      type="number"
                      defaultValue={settings?.thresholds.consecutiveAbsences || 2}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Participation Drop %</Label>
                    <Input
                      type="number"
                      defaultValue={settings?.thresholds.participationDropPercentage || 30}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Grade Drop %</Label>
                    <Input
                      type="number"
                      defaultValue={settings?.thresholds.gradeDropPercentage || 20}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Missed Assignments</Label>
                    <Input
                      type="number"
                      defaultValue={settings?.thresholds.missedAssignments || 2}
                    />
                  </div>
                </div>
              </div>

              <div className="space-y-4">
                <h3 className="font-semibold">Notification Settings</h3>
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <Label>Email Notifications</Label>
                    <Switch defaultChecked={settings?.notifications.emailEnabled} />
                  </div>
                  <div className="flex items-center justify-between">
                    <Label>Dashboard Notifications</Label>
                    <Switch defaultChecked={settings?.notifications.dashboardEnabled} />
                  </div>
                  <div className="flex items-center justify-between">
                    <Label>Department Sharing</Label>
                    <Switch defaultChecked={settings?.notifications.departmentSharingEnabled} />
                  </div>
                </div>
              </div>

              <Button>Save Settings</Button>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Intervention Modal */}
      {selectedStudent && (
        <InterventionModal
          open={showInterventionModal}
          onClose={() => setShowInterventionModal(false)}
          studentId={selectedStudent}
          studentName={
            riskStudents.find((s) => s.studentId === selectedStudent)?.studentName || 'Student'
          }
          onSubmit={(data) =>
            createIntervention.mutate({
              studentId: selectedStudent,
              ...data,
            })
          }
        />
      )}
    </div>
  );
}
