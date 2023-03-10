import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Metrics from './components/Metrics';
import Results from './components/Results';
import { IEvent } from 'interfaces/analysis';
import { IRouterParams } from 'interfaces/project';
import { getEventDetail, operateCollection } from 'services/analysis';
import { ITarget } from 'interfaces/targeting';

interface IProps {
  trackEvents: boolean;
  allowEnableTrackEvents: boolean;
  targeting?: ITarget;
  initTargeting(): void;
}

const Analysis = (props: IProps) => {
  const { trackEvents, targeting, allowEnableTrackEvents, initTargeting } = props;
  const [ eventInfo, saveEventInfo ] = useState<IEvent>();
  const [ submitLoading, saveSubmitLoading ] = useState<boolean>(false);
  const { projectKey, environmentKey, toggleKey } = useParams<IRouterParams>();

  const getEvent = useCallback(() => {
    getEventDetail<IEvent>(projectKey, environmentKey, toggleKey).then(res => {
      if (res.success && res.data) {
        saveEventInfo(res.data);
      }
    });
  }, [environmentKey, projectKey, toggleKey]);

  useEffect(() => {
    getEvent();
  }, [getEvent]);

  const operateTrackCollection = useCallback(trackEvents => {
    saveSubmitLoading(true);
    operateCollection(projectKey, environmentKey, toggleKey, {
      trackAccessEvents: trackEvents
    }).then(res => {
      if (res.success) {
        initTargeting();
      }
      saveSubmitLoading(false);
    });
  }, [environmentKey, projectKey, toggleKey, initTargeting]);

  return (
    <div>
      <Metrics 
        eventInfo={eventInfo}
        getEvent={getEvent}
        initTargeting={initTargeting}
      />
      <Results 
        eventInfo={eventInfo}
        targeting={targeting}
        trackEvents={trackEvents}
        submitLoading={submitLoading}
        allowEnableTrackEvents={allowEnableTrackEvents}
        initTargeting={initTargeting}
        saveSubmitLoading={saveSubmitLoading}
        operateTrackCollection={operateTrackCollection}
      />
    </div>
  );
};

export default Analysis;
