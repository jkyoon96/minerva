'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Intervention } from '@/types/analytics';

interface InterventionModalProps {
  open: boolean;
  onClose: () => void;
  studentId: number;
  studentName: string;
  onSubmit: (data: {
    type: Intervention['type'];
    description: string;
  }) => Promise<void>;
}

export function InterventionModal({
  open,
  onClose,
  studentId,
  studentName,
  onSubmit,
}: InterventionModalProps) {
  const [type, setType] = useState<Intervention['type']>('NOTE');
  const [description, setDescription] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async () => {
    if (!description.trim()) return;

    setIsSubmitting(true);
    try {
      await onSubmit({ type, description });
      setDescription('');
      setType('NOTE');
      onClose();
    } catch (error) {
      // Error handling is done in parent
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add Intervention Record</DialogTitle>
          <DialogDescription>
            Record an intervention for {studentName}
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          <div className="space-y-2">
            <Label htmlFor="intervention-type">Intervention Type</Label>
            <Select value={type} onValueChange={(v) => setType(v as Intervention['type'])}>
              <SelectTrigger id="intervention-type">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="EMAIL">Email Sent</SelectItem>
                <SelectItem value="MEETING">1:1 Meeting</SelectItem>
                <SelectItem value="NOTE">Note/Memo</SelectItem>
                <SelectItem value="ALERT_ESCALATION">Alert Escalation</SelectItem>
                <SelectItem value="RESOLVED">Issue Resolved</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              placeholder="Enter intervention details..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={5}
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting || !description.trim()}>
            {isSubmitting ? 'Saving...' : 'Save Intervention'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
